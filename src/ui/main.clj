(ns ui.main
  (:require [fn-fx.fx-dom :as dom]
            [clojure.pprint :refer [cl-format]]
            [environment.start-environment :as env]
            [nodes.struct :as struct]
            [nodes.relations :as relations]
            [fn-fx.diff :refer [component defui render should-update?]]
            [fn-fx.controls :as ui]))

(defn getCorrectInformationForParameter [state, parameterId]
  (let [parameterName (struct/get-node-property state parameterId :name)
        value (struct/get-node-property state parameterId :value)]
    (if parameterName
      (str parameterName)
      (if (string? value)
        (str "\"" value "\"(const)")
        (str value "(const)"))
      )))

(defn findParametersForFunction [state functionId]
  (reduce #(str %1 "," %2)
          (map #(str (getCorrectInformationForParameter state %))
               (into [] (struct/get-node-property state functionId :parameters))))
  )

(defn findParametersForCallFunction [state functionId]
  (reduce #(str %1 "," %2)
          (map #(getCorrectInformationForParameter state %)
               (struct/get-node-property state functionId :parameter-map)))
  )

(defn findFunctionsForNamespace [state namespaceId]
  (filter #(relations/is-type-function-def state %) (into [] (struct/get-node-property state namespaceId :scope-children)))
  )

(defn getFullNamespaceName [state namespaceId]
  (let [parentNode (struct/get-node-property state namespaceId :parent-namespace)
        childName (struct/get-node-property state namespaceId :name)]
    (if parentNode
      (str (getFullNamespaceName state parentNode) "." childName)
      childName))
  )

(struct/get-all-nodes env/start-environment)

(defn findFunctionCalls [state functionId]
  (let [functionCall (struct/get-node-property state functionId :result)]
    (if functionCall (let [called (struct/get-node-property state functionCall :called)
                           calledFunctionName (struct/get-node-property state called :name)
                           parametersForCallFunc (findParametersForCallFunction state functionCall)]
                       (str " -> " calledFunctionName "(" parametersForCallFunc ")")))))

(defn getConstantsForFunction [state functionId]
  (let [scopeChildren (into [] (struct/get-node-property state functionId :scope-children))]
    (reduce #(str %1 "\n\t\t" %2)
            ""
            (map #(let [name (struct/get-node-property state % :name)
                        value (struct/get-node-property state % :value)]
                    (str "const "
                         (if name
                           (str name ": " value)
                           (str value))))
                 (filter #(= (struct/get-node-property state % :type) :constant) scopeChildren)))))

(defn renderFunctionContent [state functionId]
  (let [functionName (struct/get-node-property state functionId :name)
        functionParameters (findParametersForFunction state functionId)
        functionCalls (findFunctionCalls state functionId)]
    (str " function " functionName "(" functionParameters ")" functionCalls
         "\t\t" (getConstantsForFunction state functionId))))

(defn getFunctionsAndTheirParameters [state namespaceId]
  (reduce #(str %1 "\n\t" %2)
          (map #(renderFunctionContent state %) (findFunctionsForNamespace state namespaceId)))
  )

(defn findNameSpaces [state]
  (filter #(= (struct/get-node-property state % :type) :namespace) (struct/get-all-nodes-keys state))
  )
env/start-environment
(defn pprint [state]
  (reduce #(str %1 "\n\n" %2)
          (map #(str " namespace " (getFullNamespaceName state %) "\n\t" (getFunctionsAndTheirParameters state %))
               (findNameSpaces state)))
  )
(defn create-mock-ui-component [name]
  (ui/menu :text name
           :items [(ui/menu-item :text name :on-action {:event :mock :mock-name name})]))
(defui TheMenu
       (render [this _]
               (ui/menu-bar
                 :menus [(ui/menu :text "File"
                                  :items [(ui/menu-item :text "New"
                                                        :on-action {:event :file-new})
                                          (ui/menu-item :text "Open"
                                                        :on-action {:event :file-open})


                                          (ui/menu-item :text "Exit"
                                                        :on-action {:event :file-exit})
                                          ])
                         (create-mock-ui-component "Navigate")
                         (create-mock-ui-component "Refactor")
                         (create-mock-ui-component "Cleanup")
                         (create-mock-ui-component "Preferences")
                         (create-mock-ui-component "Run")

                         (ui/menu :text "Help"
                                  :items [(ui/menu-item :text "About"
                                                        :on-action {:event :help-exit})])])))

(defui CodeBox
       (render [with-precision state]
               (ui/label
                 :text (pprint state)))
       )
(defui MainWindow
       (render [this state]
               (ui/v-box
                 :min-width 640
                 :min-height 680
                 :children [(the-menu state)
                            (code-box state)])))


(defui MainStage
       (render [this args]
               (ui/stage
                 :title "ast-editor"
                 :shown true
                 :scene (ui/scene
                          :root (main-window args)))))

(defn -main []
  (let [state (atom env/start-environment)
        handler-fn (fn [event]
                     (println event))
        ui-state (atom (dom/app (main-stage @state) handler-fn))]

    (add-watch state :ui (fn [k r os ns]
                           (swap! ui-state
                                  (fn [old-ui]
                                    (dom/update-app old-ui (main-stage ns))))))))

(-main)

(ns ui.main
  (:require [fn-fx.fx-dom :as dom]
            [clojure.pprint :refer [cl-format]]
            [environment.start-environment :as env]
            [nodes.struct :as struct]
            [nodes.relations :as relations]
            [fn-fx.diff :refer [component defui render should-update?]]
            [fn-fx.controls :as ui]))

(defn findParametersForFunction [state functionId]
  (reduce #(str %1 "," %2)
          (map #(str (struct/get-node-property state % :name))
               (into [] (struct/get-node-property state functionId :parameters))))
  )

(defn findFunctionsForNamespace [namespace state]
  (filter #(relations/is-type-function-def state %) (into []  (:scope-children namespace)))
  )

(defn getFullNamespaceName [state namespaceId]
  (let [parentNode (struct/get-node-property state namespaceId :parent-namespace)
        childName (struct/get-node-property state namespaceId :name)]
    (if parentNode
      (str (getFullNamespaceName state parentNode) "." childName)
      childName))
  )

(defn getFunctionsAndTheirParameters [namespace state]
  (reduce #(str %1 "\n" %2)
          (map #(str " function " (struct/get-node-property state % :name) "(" (findParametersForFunction state %) ")") (findFunctionsForNamespace namespace state)))
  )

(defn findNameSpaces [state]
  (filter #(= (:type %) :namespace) (struct/get-all-nodes state))
  )

(defn pprint [state]
  (reduce #(str %1 "\n\n" %2)
          (map #(str " namespace " (:name %) "\n\t" (getFunctionsAndTheirParameters % state))
               (findNameSpaces state)))
  )

(defui TheMenu
       (render [this _]
               (ui/menu-bar
                 :menus [(ui/menu :text "File"
                                  :items [(ui/menu-item :text "New"
                                                        :on-action {:event :file-new})
                                          (ui/menu-item :text "Open"
                                                        :on-action {:event :file-open})
                                          (ui/menu-item :text "Exit"
                                                        :on-action {:event :file-exit})])
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
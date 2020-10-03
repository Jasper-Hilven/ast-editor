(ns ui.main
  (:require [fn-fx.fx-dom :as dom]
            [clojure.pprint :refer [cl-format]]
            [operations.smart :as smart]
            [nodes.struct :as struct]
            [ui.text-render :as text-render]
            [nodes.relations :as relations]
            [fn-fx.diff :refer [component defui render should-update?]]
            [fn-fx.controls :as ui]))

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
                 :text (text-render/pprint state)))
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

(def start-state (:result (smart/replace-with-direct-function-call environment.start-environment/start-environment 6 "HelloFunc")))
(pprint start-state)
(relations/find-namespaces start-state)
(struct/get-all-nodes-keys start-state)
(defn -main []
  (let [state (atom start-state)
        handler-fn (fn [event]
                     (println event))
        ui-state (atom (dom/app (main-stage @state) handler-fn))]

    (add-watch state :ui (fn [k r os ns]
                           (swap! ui-state
                                  (fn [old-ui]
                                    (dom/update-app old-ui (main-stage ns))))))))

(-main)

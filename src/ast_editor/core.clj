(ns ast-editor.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(def sample-struct {
                    :latest 4
                    :holes  []
                    0       {:type  :namespace
                             "zero" 4}
                    1       {:type  :funccal
                             :calls 2}
                    2       {:type        :func
                             :params      []
                             :result-eval 3}
                    3       {:type          :constant
                             :value         1
                             :constant-type :int}
                    4       {:type    :namespace
                             "simple" 1}})


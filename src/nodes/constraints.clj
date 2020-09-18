(ns nodes.constraints)

(def types [:namespace :funcdef :funccall :constant :parameter :sexpr :scopecontainer])
(def type-parents {:funcdef   #{:sexpr :scopecontainer}
                   :parameter #{:sexpr}
                   :funccall  #{:sexpr}
                   :constant  #{:sexpr}
                   :namespace #{:scopecontainer}})
(defn in? [coll elm] (some #(= elm %) coll))

(def constraints [
                  {:id   :parameter-map
                   :name "Expressions called as parameter for a function is a usage"
                   :type :bi
                   :from {:type  :funccall :name "parameter-map"
                          :arity :0_to_n :collection-type :list}
                   :to   {:type  :sexpr :name "used-as-parameter"
                          :arity :0_to_n :collection-type :set}}

                  {:id   :scope-children
                   :name "Expressions within a function are scope children"
                   :type :bi
                   :from {:type  :scopecontainer :name "scope-children"
                          :arity :0_to_n :collection-type :set}
                   :to   {:type  :sexpr :name "parent-scope"
                          :arity :0_to_1}
                   }
                  {:id   :func-call
                   :name "Callee of a function"
                   :type :bi
                   :from {:type  :funcdef :name "calls"
                          :arity :0_to_n :collection-type :set}
                   :to   {:type                      :funccall
                          :name                      "called"
                          :arity                     :1
                          :constructor-argument-name "function"
                          }}
                  {:id   :function-parameters
                   :name "Parameters of a function"
                   :type :bi
                   :from {:type  :funcdef :name "parameters"
                          :arity :0_to_n :collection-type :list}
                   :to   {:type                      :parameter :name "function"
                          :arity                     :1
                          :constructor-argument-name "function"}
                   }
                  {:id   :function-result
                   :name "Result of a function"
                   :type :bi
                   :from {:type  :funcdef :name "result"
                          :arity :0_to_1}
                   :to   {:type  :sexpr :name "function-result"
                          :arity :0_to_n :collection-type :set}
                   }

                  {:id   :namespace-hierarchy
                   :name "Namespace hierarchy"
                   :type :bi
                   :from {:type  :namespace :name "namespace-children"
                          :arity :0_to_n :collection-type :set}
                   :to   {:type  :namespace :name "parent-namespace"
                          :arity :0_to_1}}
                  ])
(defn is-me-or-parent [me to-test]
  (or (= me to-test)
      (and (type-parents me)
           (reduce #(or % (is-me-or-parent %2 to-test)) false (type-parents me)))))
(def standalone-types
  (filter #(not (in? (vals type-parents) %)) types))
(defn get-all-relevant-constraints [node-type]
  (filter #(or (= (get-in % [:from :type]) node-type)
               (is-me-or-parent node-type (get-in % [:to :type])))
          constraints))

(defn get-onesided-specs [constraint] [(:from constraint) (:to constraint)])
(defn get-all-onesided-specs [] (flatten (map get-onesided-specs constraints)))
(defn get-all-relevant-onesided-specs [node-type] (filter #(is-me-or-parent node-type (:type %)) (get-all-onesided-specs)))
(defn get-constraint-with-id [constraint-id] (first (filter #(= constraint-id (:id %)) constraints)))

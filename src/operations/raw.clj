(ns operations.raw
  (:require [nodes.struct :refer :all]
            [nodes.constraints :refer :all]))

(defn get-default-create-value-multiarity [onesided-spec leaf-node-type]
  (let [collection-type (:collection-type onesided-spec)]
    (cond (= :set collection-type) #{}
          (= :list collection-type) [])))
(defn get-all-default-create-properties [node-type]
  (let [relevant-onesided (get-all-relevant-onesided-specs node-type)
        multiarities (filter #(= (:arity %) :0_to_n) relevant-onesided)]
    (reduce #(assoc % (keyword (:name %2))
                      (get-default-create-value-multiarity %2 node-type))
            {} multiarities)))

(defn create-node-of-type-with-default-properties [struct nodetype]
  (add-node-of-type-and-propertymap struct
                                    nodetype
                                    (get-all-default-create-properties nodetype)))
(defn create-empty-namespace [struct] (create-node-of-type-with-default-properties struct :namespace))
(defn create-empty-funcdef [struct] (create-node-of-type-with-default-properties struct :funcdef))
(defn create-empty-parameter [struct] (create-node-of-type-with-default-properties struct :parameter))
(defn create-empty-funccall [struct] (create-node-of-type-with-default-properties struct :funccall))
(defn create-empty-constant [struct] (create-node-of-type-with-default-properties struct :constant))

(defn add-scope-child [struct scope-child scope-container]
  (-> struct
      (update-node-property scope-container :scope-children #(conj % scope-child))
      (set-node-property scope-child :parent-scope scope-container)))

(defn set-function-name [struct function name]
  (set-node-property struct function :name name))
(defn set-namespace-name [struct namespace name]
  (set-node-property struct namespace :name name))

(defn add-new-parameter-to-function [struct function parameter-name]
  (let [parameter-created (create-empty-parameter struct)
        parameter-key (:node-key parameter-created)]
    (-> (:struct parameter-created)
        (set-node-property parameter-key :name parameter-name)
        (set-node-property parameter-key :function function)
        (add-scope-child parameter-key function)
        (update-node-property function :parameters #(conj % parameter-key)))))

(defn add-new-parameters-to-function [struct function parameters]
  (reduce #(add-new-parameter-to-function %1 function %2) struct parameters))

(defn create-func-deprecated [struct funcname scope-container parameters]
  (let [created (create-empty-funcdef struct)
        created-func-key (:node-key created)]
    (-> (:struct created)
        (set-function-name created-func-key funcname)
        (add-scope-child created-func-key scope-container)
        (add-new-parameters-to-function created-func-key parameters)
        )))
(defn create-func [struct funcname scope-container parameters]
  (let [created (create-empty-funcdef struct)
        created-func-key (:node-key created)]
    {:struct (-> (:struct created)
                 (set-function-name created-func-key funcname)
                 (add-scope-child created-func-key scope-container)
                 (add-new-parameters-to-function created-func-key parameters)
                 )
     :node-key created-func-key}))

(defn add-function-call-relation [struct function function-call]
  (-> struct
      (set-node-property function-call :called function)
      (update-node-property function :calls #(conj % function-call)))
  )

(defn add-parameter-map-relation [struct function-call parameter]
  (-> struct
      (update-node-property function-call :parameter-map #(conj % parameter))
      (update-node-property parameter :used-as-parameter #(conj % function-call))))

(defn add-namespace-parent [struct child-namespace parent-namespace]
  (-> struct
      (update-node-property parent-namespace :namespace-children #(conj % child-namespace))
      (set-node-property child-namespace :parent-namespace parent-namespace)))
(defn add-parameter-map-relations [struct function-call parameters]
  (reduce #(add-parameter-map-relation % function-call %2) struct parameters))

(defn create-function-call [struct function scope-container parameter-values]
  (let [created (create-empty-funccall struct)
        created-key (:node-key created)]
    {:struct   (-> (:struct created)
                   (add-function-call-relation function created-key)
                   (add-scope-child created-key scope-container)
                   (add-parameter-map-relations created-key parameter-values))
     :node-key created-key}))

(defn set-as-function-result [struct function result]
  {:struct (-> struct (update-node-property result :function-result #(conj % function))
               (set-node-property function :result result))})
(create-empty-namespace (get-empty-struct))

(defn create-namespace
  ([struct name parent]
   (let [empty-namespace-created (create-empty-namespace struct)
         empty-namespace-created-struct (:struct empty-namespace-created)
         empty-namespace-created-child (:node-key empty-namespace-created)]
     {:struct   (-> empty-namespace-created-struct
                    (set-namespace-name empty-namespace-created-child name)
                    (add-namespace-parent empty-namespace-created-child parent))
      :node-key empty-namespace-created-child}))
  ([struct name]
   (let [empty-namespace-created (create-empty-namespace struct)
         empty-namespace-created-struct (:struct empty-namespace-created)
         empty-namespace-created-child (:node-key empty-namespace-created)]
     {:struct   (set-namespace-name empty-namespace-created-struct empty-namespace-created-child name)
      :node-key empty-namespace-created-child})))

(defn create-constant [struct constant-value]
  (let [constant-add (create-empty-constant struct)]
    "todo"))

(def empt (get-empty-struct))
(def func-created (-> (:struct (create-empty-namespace (get-empty-struct)))
                      (create-func-deprecated "hello-world" 0 ["Param1" "Param2"])))
(def func-called (-> func-created (create-function-call 1 1 [2 3])))
(def set-as-result (-> func-called (set-as-function-result 1 4)))
(add-scope-child func-created 1 0)
(def withConstant (create-empty-constant func-created))
(add-scope-child (:struct withConstant) 2 1)

;;(add-scope-child func-created 1 0)
(get-all-default-create-properties :funcdef)

(-> empt (create-empty-namespace))
(-> empt (create-namespace "Root") (:struct) (create-namespace "Jos" 0))


{:next 4,
 :holes [],
 :nodes {0 {:scope-children #{1}, :namespace-children #{}, :type :namespace},
         1 {:used-as-parameter #{},
            :scope-children #{},
            :calls #{},
            :parameters [2 3],
            :function-result #{},
            :type :funcdef,
            :name "hello-world",
            :parent-scope 0},
         2 {:used-as-parameter #{}, :function-result #{}, :type :parameter, :name "Param1", :function 1},
         3 {:used-as-parameter #{}, :function-result #{}, :type :parameter, :name "Param2", :function 1}}}


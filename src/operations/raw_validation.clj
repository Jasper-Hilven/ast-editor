(ns operations.raw-validation
  (:require [nodes.relations :as rel]))
(defn can-add-scope-child? [struct scope-child scope-container]
  (and (not (rel/has-scope-parent struct scope-child))
       (rel/is-type-scope-container struct scope-container)))

(defn can-set-function-name? [struct function name]
  (and (rel/is-type-function-def struct function)))
(defn can-add-new-parameter-to-function? [struct function parameter-name]
  (and (rel/is-type-function-def struct function)))
(defn can-add-new-parameters-to-function? [struct function parameters]
  (reduce #(and % (can-add-new-parameter-to-function? struct function %2))
          true
          parameters))

(defn can-create-empty-func? [struct funcname scope-container parameters]
  (and (rel/is-type-scope-container struct scope-container)))

(defn can-create-ns?
  ([struct name parent]
   (rel/is-type-namespace struct parent))
  ([struct name] true))

(defn can-add-parameter-map-relation? [struct function-call parameter] true)
(defn can-add-parameter-map-relations? [struct function-call parameters] true)
(defn can-create-function-call? [struct function scope-container parameter-values] true)
(defn can-create-constant? [struct constant-value scope-container]
  (rel/is-type-scope-container struct scope-container))
(defn can-set-as-function-result? [struct function result]
  (and (rel/is-type-function-def struct function)
       (rel/is-type-s-expr struct result)))

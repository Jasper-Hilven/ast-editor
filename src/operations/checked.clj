(ns operations.checked
  (:require [operations.raw :as r]
            [operations.raw-validation :as v]))

;;Checked operations is a check on whether the raw operations is possible + the execution of the raw operation
(defn resultify [result] {:result result})
(defn errorify [message] {:error message})
(defn cond-result-error [params cond-func result-func error-message]
  (if (apply cond-func params)
    (resultify (apply result-func params))
    (errorify error-message)))
(defn get-node-result [result] (:node-key (:result result)))
(defn get-struct-result [result] (:struct (:result result)))
(defn chain-opp
  [struct & operations] (reduce #(:struct (:result (%2 %1))) struct operations))
(defn add-scope-child [struct scope-child scope-container]
  (cond-result-error [struct scope-child scope-container]
                     v/can-add-scope-child?
                     r/add-scope-child
                     "Cannot add scope child"))

(defn set-function-name [struct function name]
  (cond-result-error [struct function name]
                     v/can-set-function-name?
                     r/set-function-name
                     "Not a function"))

(defn add-new-parameter-to-function [struct function parameter-name]
  (cond-result-error [struct function parameter-name]
                     v/can-add-new-parameter-to-function?
                     r/add-new-parameter-to-function
                     "Cannot add parameter as it is not a function"))

(defn add-new-parameters-to-function [struct function parameters]
  (cond-result-error [struct function parameters]
                     v/can-add-new-parameters-to-function?
                     r/add-new-parameters-to-function
                     "Cannot add parameters as it is not a function"))

(defn create-func [struct funcname scope-container parameters]
  (cond-result-error [struct funcname scope-container parameters]
                     v/can-create-empty-func?
                     r/create-func
                     "An invalid scope container is given"))
(defn create-namespace
  ([struct name]
   (cond-result-error [struct name]
                      v/can-create-ns?
                      r/create-namespace
                      "Cannot create namespace"))
  ([struct name parent]
   (cond-result-error [struct name parent]
                      v/can-create-ns?
                      r/create-namespace
                      "Cannot create namespace")))

(defn create-constant [struct constant-value scope-container]
  (cond-result-error [struct constant-value scope-container]
                     v/can-create-constant?
                     r/create-constant
                     "Cannot create constant"))

(defn add-parameter-map-relation [struct function-call parameter] true)
(defn add-parameter-map-relations [struct function-call parameters] true)
(defn create-function-call [struct scope-container parameter-values]
  (cond-result-error [struct scope-container parameter-values]
                     v/can-create-function-call?
                     r/create-function-call
                     "The function call cannot be created"))
(defn set-as-function-result [struct function result]
  (cond-result-error [struct function result]
                     v/can-set-as-function-result?
                     r/set-as-function-result
                     "The result cannot be set for the given function"))
(defn replace-all-usages-with [struct to-replace-expression with-expression]
  (cond-result-error [struct to-replace-expression with-expression]
                     v/can-replace-all-usages-with?
                     r/replace-all-usages-with
                     "Both the to replace and the with expression should be s expressions"))
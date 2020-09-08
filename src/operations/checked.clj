(ns operations.checked
  (:require [operations.raw :as r]
            [operations.raw-validation :as v]))
(defn resultify [result] {:result result})
(defn errorify [message] {:error message})
(defn cond-result-error [params cond-func result-func error-message]
  (if (apply cond-func params)
    (resultify (apply result-func params))
    (errorify error-message)))

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

;;TODO FROM HERE TO REST OF FILE

(defn add-function-call-relation [struct function function-call] true)
(defn add-parameter-map-relation [struct function-call parameter] true)
(defn add-parameter-map-relations [struct function-call parameters] true)
(defn create-function-call [struct function scope-container parameter-values] true)
(defn set-as-function-result [struct function result] true)

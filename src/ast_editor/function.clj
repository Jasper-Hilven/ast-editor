(ns ast-editor.function
  (:require [ast-editor.struct :refer :all]))

(defn add-function-node [struct]
  (add-node-of-type-and-propertymap struct :function
                                    {:scope-children {}
                                     :parameters     '()
                                     :usages         #{}}))

(defn get-function-parameters [struct function]
  (get-node-property struct function :parameters))
(defn add-function-parameter [struct function]
  (let [added (add-node-of-type-and-propertymap struct :parameter {:function function})
        struct-added (:struct added)
        parameter (:node-key added)]
    {:struct   (update-node-property struct-added function :parameter #(conj % parameter))
     :node-key parameter}))
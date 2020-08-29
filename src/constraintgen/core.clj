(ns constraintgen.core
  (:require [constraintgen.constraints :refer :all]
            [constraintgen.operations :refer :all]))


(defn arity-to-add-expectation [arity to-add]
  (cond (= arity :0_to_n) {:type :does-not-contain}))

(defn remove-relation-from-node [collection relation node other])


(get-all-relevant-constraints :funcdef)

(get-all-relevant-onesided-specs :funcdef)
(get-all-default-create-properties :namespace)


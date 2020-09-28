(ns nodes.relations
  (:require [nodes.struct :refer :all]
            [nodes.constraints :refer :all]))

(defn get-scope-parent [struct node]
  (get-node-property struct node :parent-scope))
(defn has-scope-parent [struct node]
  (nil? (get-scope-parent struct node)))
(defn is-my-or-parent-type [struct node type-to-check]
  (is-me-or-parent (get-node-type struct node) type-to-check))
(defn is-type-scope-container [struct node]
  (is-my-or-parent-type struct node :scopecontainer))
(defn is-type-function-def [struct node]
  (is-my-or-parent-type struct node :funcdef))
(defn is-type-s-expr [struct node]
  (is-my-or-parent-type struct node :sexpr))
(defn is-type-function-call [struct node]
  (is-my-or-parent-type struct node :funccall))
(defn is-type-namespace [struct node]
  (is-my-or-parent-type struct node :namespace))

(defn get-parameter-usage [struct node]
  (get-node-property struct node :used-as-parameter))
(defn get-parameter-map [struct funccall]
  (get-node-property struct funccall :parameter-map))


(defn get-parent-s-expression-scope [struct expression]
  ()

  )
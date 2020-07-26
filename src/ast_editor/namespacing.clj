(ns ast-editor.namespacing
  (:require [ast-editor.struct :refer :all]
            [ast-editor.assertion :refer :all]))

(defn get-namespace-children [struct namespace] (get-node-property struct namespace :namespace-children))
(defn get-node-namespace [struct node] (get-node-property struct node :namespace-parent))
(defn get-node-with-name [struct namespace name]
  (let [children (get-namespace-children struct namespace)]
    (get children name)))

(defn add-namespace-node [struct]
  (add-node-of-type-and-propertymap struct :namespace {:namespace-children {}}))

(defn add-node-to-namespace [struct node namespace name]
  (-> struct
      (update-node namespace #(assoc-in % [:namespace-children name] node))
      (update-node node #(assoc % :namespace-parent namespace))))

(defn remove-name-from-namespace [struct namespace name]
  (let [node (get-node-with-name struct namespace name)]
    (-> struct
        (update-node-property namespace :namespace-children #(dissoc % name))
        (remove-node-property node :namespace-parent))))


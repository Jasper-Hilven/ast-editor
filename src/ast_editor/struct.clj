(ns ast-editor.struct)
;;Creation
(defn get-empty-struct [] {:next 0 :holes [] :nodes {}})

;;Info
(defn get-all-nodes-keys [structure] (filter number? (keys (:nodes structure))))
(defn get-all-nodes [structure] (map (:nodes structure) (get-all-nodes-keys structure)))
(defn get-node-property [structure node property] (get-in structure [:nodes node property]))
(defn get-node-type [structure node] (get-node-property structure node :type))

;;Operations
(defn increase-next [struct] (assoc struct :next (inc (:next struct))))
(defn add-node [struct node]
  (let [next (:next struct)
        increased (increase-next struct)
        next-struct (assoc-in increased [:nodes next] node)]
    {:struct next-struct :node-key next}))
(defn add-node-of-type [struct type] (add-node struct {:type type}))
(defn add-node-of-type-and-propertymap [struct type property-map]
  (add-node struct (assoc property-map :type type)))

(defn update-node [struct node function]
  (update-in struct [:nodes node] function))
(defn set-node-property [struct node property value]
  (assoc-in struct [:nodes node property] value))

(defn update-node-property [struct node property function]
  (update-in struct [:nodes node property] function))

(defn remove-node-property [struct node property]
  (update-node struct node #(dissoc % property)))
(defn remove-node [struct node-key]
  (-> struct
      (update-in [:nodes] dissoc node-key)
      (update-in [:holes] conj node-key)))
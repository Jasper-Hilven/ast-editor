(ns ast-editor.struct)

(defn get-empty-struct [] {:next 0 :holes [] :nodes {}})
(defn increase-next [struct] (assoc struct :next (inc (:next struct))))
(defn add-node [struct node]
  (let [next (:next struct)
        increased (increase-next struct)
        next-struct (assoc-in increased [:nodes next] node)]
    {:struct next-struct :node-key next}))
(defn get-all-nodes-keys [structure] (filter number? (keys (:nodes structure))))
(defn get-all-nodes [structure] (map (:nodes structure) (get-all-nodes-keys structure)))
(defn remove-node [struct node-key]
  (-> struct
      (update-in [:nodes] dissoc node-key)
      (update-in [:holes] conj node-key)))
(ns constraintgen.operator
  (:require [nodes.struct :refer :all]
            [constraintgen.operations :as op]))

(def assertion-types [:assert-type
                      :assert-connected
                      :assert-empty
                      :assert-not-connected])
(def action-types [:set-connection
                   :remove-connection
                   :remove-node
                   :create-node])
(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))



(defn is-towards-node-in-collection? [store property-name node collection-type towards-node collection-type-map data]
  (let [node-type (get-node-type store node)]
    (cond (or (= collection-type :list) (= collection-type :set))
          (in? (get-node-property store node property-name) towards-node)
          )))

(defn set-towards-node-in-collection [store property-name node collection-type towards-node collection-type-map data]
  (let [node-type (get-node-type store node)]
    (cond (or (= collection-type :list) (= collection-type :set))
          (update-node-property store node property-name #(conj % towards-node))

          (= collection-type :depends-on-child-type)
          (set-towards-node-in-collection store
                                          property-name
                                          node
                                          (node-type collection-type-map)
                                          towards-node
                                          collection-type-map
                                          data))))
(defn do-assertion-assert-connected
  [store connected-assertion]
  (let [node (:from connected-assertion)
        arity (:arity connected-assertion)
        property-name (:name connected-assertion)
        collection-type (:collection-type connected-assertion)
        towards-node (:to connected-assertion)
        collection-type-map (:collection-type-map connected-assertion)
        data (:data connected-assertion)]
    (if (or (= arity :0_to_1) (= arity :1))
      (= (get-node-property store node property-name) towards-node)
      (is-towards-node-in-collection? store property-name node collection-type towards-node collection-type-map data))))

(defn do-assertion [store assertion]
  (let [assertion-type (:type assertion)]
    (cond (= assertion-type :assert-type)
          {:success (= (:should-be assertion) (get-node-type store (:node assertion)))}
          (= assertion-type :assert-connected)
          {:success (do-assertion-assert-connected store assertion)})
    :else {:success :not-implemented}))

(defn do-action [store action]
  (let [action-type (:type action)
        relation (:name action)]
    (cond (= action-type :set-connection)
          (throw "not implemented"                          ;;set-towards-node-in-collection store
                 )
          (= action-type :remove-connection)
          (remove-node-property store (:node action) ())
          (= action-type :remove-node)
          (remove-node store (:node-id action))
          (= action-type :create-node)
          (add-node-of-type-and-propertymap store (:node-type action) (:node-properties action)))))

(defn do-operation [store operation]
  (let [operation-type (:type operation)]
    (cond (in? assertion-types operation-type)
          {:assertion (do-assertion store operation)}
          (in? action-types operation-type)
          {:after-action (do-action store operation)})))


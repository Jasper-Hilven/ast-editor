(ns constraintgen.operations
  (:require [nodes.constraints :refer :all]))

(defn get-default-create-value-multiarity [onesided-spec leaf-node-type]
  (let [collection-type (:collection-type onesided-spec)]
    (cond (= :set collection-type) #{}
          (= :list collection-type) [])))

(defn get-all-default-create-properties [node-type]
  (let [relevant-onesided (get-all-relevant-onesided-specs node-type)
        multiarities (filter #(= (:arity %) :0_to_n) relevant-onesided)]
    (reduce #(assoc % (keyword (:name %2))
                      (get-default-create-value-multiarity %2 node-type))
            {} multiarities)))

(defn throw-invalid-node [] (throw (new Exception "Invalid node type")))

(defn create-node [node-type]
  (if (not (in? standalone-types node-type))
    (throw-invalid-node)
    (let [default-create-properties (get-all-default-create-properties node-type)]
      '({:type            :create-node
         :node-type       node-type
         :node-properties default-create-properties}))))

(create-node :funcdef)

(defn remove-node [id node-type]
  (let [specs (get-all-relevant-onesided-specs node-type)
        multi-arities (filter #(= :0_to_n
                                  (:arity %))
                              specs)
        multi-arity-operations (map (fn [single-spec]
                                      {:type            :assert-empty
                                       :name            (:name single-spec)
                                       :node            id
                                       :collection-type (:collection-type single-spec)})
                                    multi-arities)
        single-arities (filter #(= :0_to_1
                                   (:arity %))
                               specs)
        single-arity-operations (map (fn [single-spec]
                                       {:type            :assert-not-connected
                                        :name            (:name single-spec)
                                        :node            id
                                        :collection-type (:collection-type single-spec)})
                                     single-arities)

        ] (concat multi-arity-operations
                  single-arity-operations
                  [{:type    :remove-node
                    :node-id id}])))

(remove-node 0 :funcdef)

(defn add-relation-to-node [constraint-id from to data]
  (let [constraint (get-constraint-with-id constraint-id)
        from-con (:from constraint)
        to-con (:to constraint)
        from-arity (:arity from-con)
        to-arity (:arity to-con)]
    (list
      {:type      :assert-type
       :node      from
       :should-be (:type from-con)}

      {:type      :assert-type
       :node      to
       :should-be (:type to-con)}

      {:type            :assert-not-connected
       :node            from
       :name            (:name from-con)
       :to              to
       :arity           from-arity
       :collection-type (:collection-type from-con)
       :data            data}

      {:type            :assert-not-connected
       :node            to
       :name            (:name to-con)
       :to              from
       :arity           to-arity
       :collection-type (:collection-type to-con)
       :data            data}

      {:type  :set-connection
       :name  (:name from-con)
       :from  from
       :to    to
       :data  data
       :arity from-arity}

      {:type  :set-connection
       :name  (:name to-con)
       :from  to
       :to    from
       :data  data
       :arity from-arity})))

(defn remove-relation-from-node [constraint-id from to data]
  (let [constraint (get-constraint-with-id constraint-id)
        from-con (:from constraint)
        to-con (:to constraint)
        from-arity (:arity from-con)
        to-arity (:arity to-con)
        ]
    (list
      {:type      :assert-type
       :node      from
       :should-be (:type from-con)}

      {:type      :assert-type
       :node      to
       :should-be (:type to-con)}

      {:type            :assert-connected
       :node            from
       :name            (:name from-con)
       :to              to
       :arity           from-arity
       :collection-type (:collection-type from-con)
       :data            data}

      {:type            :assert-connected
       :node            to
       :name            (:name to-con)
       :to              from
       :arity           to-arity
       :collection-type (:collection-type to-con)
       :data            data}

      {:type :remove-connection
       :name (:name from-con)
       :from from
       :to   to
       :data data}

      {:type :remove-connection
       :name (:name to-con)
       :from to
       :to   from
       :data data}
      )))

(add-relation-to-node :parameter-map 5 6 {})
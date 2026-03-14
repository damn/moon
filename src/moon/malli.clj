(ns moon.malli
  (:require [malli.core :as m]
            [malli.error :as me]))

(def schema m/schema)
(def validate m/validate)

(defn validate-humanize [schema value]
  (when-not (m/validate schema value)
    (throw (ex-info (str (me/humanize (m/explain schema value)))
                    {:value value
                     :schema (m/form schema)}))))

(defn map-keys [map-schema]
  (let [[_m _p & ks] map-schema]
    (for [[k m? _schema] ks]
      k)))

(comment
 (= (map-keys
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    [:foo :bar :baz :boz :asdf]))

(defn- map-form-k->properties
  "Given a map schema gives a map of key to key properties (like :optional)."
  [map-schema]
  (let [[_m _p & ks] map-schema]
    (into {} (for [[k m? _schema] ks]
               [k (if (map? m?) m?)]))))

(comment
 (= (map-form-k->properties
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    {:foo nil,
     :bar nil,
     :baz {:optional true},
     :boz {:optional false},
     :asdf {:optional true}}))

(defn optional? [k map-schema]
  (:optional (k (map-form-k->properties map-schema))))

(comment
 (= (optional? :foo
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    nil)

 (= (optional? :baz
               [:map {:closed true}
                [:foo]
                [:bar]
                [:baz {:optional true}]
                [:boz {:optional false}]
                [:asdf {:optional true}]])
    true)

 (= (optional? :asdf
               [:map {:closed true}
                [:foo]
                [:bar]
                [:baz {:optional true}]
                [:boz {:optional false}]
                [:asdf {:optional true}]])
    true)
 )

(defn optional-keyset [map-schema]
  (set (filter #(optional? % map-schema)
               (map-keys map-schema))))

(comment
 (= (optional-keyset
     [:map {:closed true}
      [:foo]
      [:bar]
      [:baz {:optional true}]
      [:boz {:optional false}]
      [:asdf {:optional true}]])
    #{:baz :asdf})
 )

(defn create-map-schema
  "Can define keys as just keywords or with schema-props like [:foo {:optional true}]."
  [ks k->malli-schema-form]
  (apply vector :map {:closed true}
         (for [k ks
               :let [k? (keyword? k)
                     schema-props (if k? nil (k 1))
                     k (if k? k (k 0))]]
           (do
            (assert (keyword? k))
            (assert (or (nil? schema-props) (map? schema-props)) (pr-str ks))
            [k schema-props (k->malli-schema-form k)]))))

(ns malli.utils
  (:require [malli.utils.map-keys :as map-keys]
            [malli.utils.map-form-k-properties :refer [map-form-k->properties]]))

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
               (map-keys/f map-schema))))

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

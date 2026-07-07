(ns clojure.widget-value
  (:require [clojure.checkbox :as checkbox]
            [clojure.edn :as edn]
            [clojure.get-user-object]
            [clojure.group :as group]
            [clojure.select-box :as select-box]
            [clojure.text-field :as text-field]))

(defmulti f
  (fn [[schema-k :as _schema] widget schemas]
    schema-k))

(defn map-widget-table-get-value [table schemas]
  (into {}
        (for [widget (filter (comp vector? clojure.get-user-object/f) (group/get-children table))
              :let [[k _] (clojure.get-user-object/f widget)]]
          [k (f (get schemas k) widget schemas)])))

(defmethod f :default
  [_ widget _schemas]
  ((clojure.get-user-object/f widget) 1))

(defmethod f :s/boolean
  [_ widget _schemas]
  (checkbox/checked? widget))

(defmethod f :s/enum
  [_ widget _schemas]
  (edn/read-string (select-box/get-selected widget)))

(defmethod f :s/map
  [_ table schemas]
  (map-widget-table-get-value table schemas))

(defmethod f :s/number
  [_ widget _schemas]
  (edn/read-string (text-field/get-text widget)))

(defmethod f :s/one-to-many
  [_ widget _schemas]
  (->> (group/get-children widget)
       (keep clojure.get-user-object/f)
       set))

(defmethod f :s/one-to-one
  [_ widget _schemas]
  (->> (group/get-children widget)
       (keep clojure.get-user-object/f)
       first))

(defmethod f :s/string
  [_ widget _schemas]
  (text-field/get-text widget))

(defmethod f :s/val-max
  [_ widget _schemas]
  (edn/read-string (text-field/get-text widget)))

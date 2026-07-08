(ns clojure.editor.widget-value
  (:require [clojure.actor.get-user-object]
            [clojure.checkbox :as checkbox]
            [clojure.edn :as edn]
            [clojure.group :as group]
            [clojure.select-box :as gdx-select-box]
            [clojure.text-field :as gdx-text-field]))

(defmulti widget-value
  (fn [[schema-k :as _schema] widget schemas]
    schema-k))

(defn map-widget-table-get-value [table schemas]
  (into {}
        (for [widget (filter (comp vector? clojure.actor.get-user-object/f) (group/get-children table))
              :let [[k _] (clojure.actor.get-user-object/f widget)]]
          [k (widget-value (get schemas k) widget schemas)])))

(defmethod widget-value :default
  [_ widget _schemas]
  ((clojure.actor.get-user-object/f widget) 1))

(defmethod widget-value :s/boolean
  [_ widget _schemas]
  (checkbox/checked? widget))

(defmethod widget-value :s/enum
  [_ widget _schemas]
  (edn/read-string (gdx-select-box/get-selected widget)))

(defmethod widget-value :s/map
  [_ table schemas]
  (map-widget-table-get-value table schemas))

(defmethod widget-value :s/number
  [_ widget _schemas]
  (edn/read-string (gdx-text-field/get-text widget)))

(defmethod widget-value :s/one-to-many
  [_ widget _schemas]
  (->> (group/get-children widget)
       (keep clojure.actor.get-user-object/f)
       set))

(defmethod widget-value :s/one-to-one
  [_ widget _schemas]
  (->> (group/get-children widget)
       (keep clojure.actor.get-user-object/f)
       first))

(defmethod widget-value :s/string
  [_ widget _schemas]
  (gdx-text-field/get-text widget))

(defmethod widget-value :s/val-max
  [_ widget _schemas]
  (edn/read-string (gdx-text-field/get-text widget)))

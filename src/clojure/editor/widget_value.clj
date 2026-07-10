(ns clojure.editor.widget-value
  (:require [gdl.scenes.scene2d.actor :as actor]
            [clojure.ui.checkbox :as checkbox]
            [clojure.edn :as edn]
            [clojure.scene2d.group :as group]
            [gdl.scenes.scene2d.ui.select-box :as gdx-select-box]
            [gdl.scenes.scene2d.ui.text-field :as gdx-text-field]))

(defmulti widget-value
  (fn [[schema-k :as _schema] widget schemas]
    schema-k))

(defn map-widget-table-get-value [table schemas]
  (into {}
        (for [widget (filter (comp vector? actor/get-user-object) (group/get-children table))
              :let [[k _] (actor/get-user-object widget)]]
          [k (widget-value (get schemas k) widget schemas)])))

(defmethod widget-value :default
  [_ widget _schemas]
  ((actor/get-user-object widget) 1))

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
       (keep actor/get-user-object)
       set))

(defmethod widget-value :s/one-to-one
  [_ widget _schemas]
  (->> (group/get-children widget)
       (keep actor/get-user-object)
       first))

(defmethod widget-value :s/string
  [_ widget _schemas]
  (gdx-text-field/get-text widget))

(defmethod widget-value :s/val-max
  [_ widget _schemas]
  (edn/read-string (gdx-text-field/get-text widget)))

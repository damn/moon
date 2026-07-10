(ns clojure.editor.widget-value
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.ui.check-box :as check-box]
            [clojure.edn :as edn]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.ui.select-box :as gdx-select-box]
            [com.badlogic.gdx.scenes.scene2d.ui.text-field :as gdx-text-field]))

(defmulti widget-value
  (fn [[schema-k :as _schema] widget schemas]
    schema-k))

(defn map-widget-table-get-value [table schemas]
  (into {}
        (for [widget (filter (comp vector? actor/getUserObject) (group/getChildren table))
              :let [[k _] (actor/getUserObject widget)]]
          [k (widget-value (get schemas k) widget schemas)])))

(defmethod widget-value :default
  [_ widget _schemas]
  ((actor/getUserObject widget) 1))

(defmethod widget-value :s/boolean
  [_ widget _schemas]
  (check-box/isChecked widget))

(defmethod widget-value :s/enum
  [_ widget _schemas]
  (edn/read-string (gdx-select-box/getSelected widget)))

(defmethod widget-value :s/map
  [_ table schemas]
  (map-widget-table-get-value table schemas))

(defmethod widget-value :s/number
  [_ widget _schemas]
  (edn/read-string (gdx-text-field/getText widget)))

(defmethod widget-value :s/one-to-many
  [_ widget _schemas]
  (->> (group/getChildren widget)
       (keep actor/getUserObject)
       set))

(defmethod widget-value :s/one-to-one
  [_ widget _schemas]
  (->> (group/getChildren widget)
       (keep actor/getUserObject)
       first))

(defmethod widget-value :s/string
  [_ widget _schemas]
  (gdx-text-field/getText widget))

(defmethod widget-value :s/val-max
  [_ widget _schemas]
  (edn/read-string (gdx-text-field/getText widget)))

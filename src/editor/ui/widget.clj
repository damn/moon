(ns editor.ui.widget
  (:require [editor.ui.widget.animation :as animation]
            [editor.ui.widget.boolean :as boolean]
            [editor.ui.widget.default :as default]
            [editor.ui.widget.enum :as enum]
            [editor.ui.widget.image :as image]
            [editor.ui.widget.map :as map]
            [editor.ui.widget.number :as number]
            [editor.ui.widget.one-to-many :as one-to-many]
            [editor.ui.widget.one-to-one :as one-to-one]
            [editor.ui.widget.sound :as sound]
            [editor.ui.widget.string :as string]
            [editor.ui.widget.val-max :as val-max])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(declare create-widget widget-value)

(defn widget-value [[schema-k] widget schemas]
  (case schema-k
    :s/boolean (boolean/value widget)
    :s/enum (enum/value widget)
    :s/map (map/value widget schemas widget-value)
    :s/number (number/value widget)
    :s/one-to-many (one-to-many/value widget)
    :s/one-to-one (one-to-one/value widget)
    :s/string (string/value widget)
    :s/val-max (val-max/value widget)
    (default/value widget)))

(defn map-widget-value [table schemas]
  (map/value table schemas widget-value))

(defn- build-widget [state schema k v]
  (let [widget (create-widget state schema v)]
    (.setUserObject ^Actor widget [k v])
    widget))

(defn create-widget [state [schema-k :as schema] v]
  (case schema-k
    :s/animation (animation/create state v)
    :s/boolean (boolean/create state v)
    :s/enum (enum/create state schema v)
    :s/image (image/create state v)
    :s/map (map/create state schema v
                       (fn [schema k v] (build-widget state schema k v))
                       widget-value)
    :s/number (number/create state schema v)
    :s/one-to-many (one-to-many/create state schema v)
    :s/one-to-one (one-to-one/create state schema v)
    :s/sound (sound/create state v)
    :s/string (string/create state schema v)
    :s/val-max (val-max/create state schema v)
    (default/create state v)))

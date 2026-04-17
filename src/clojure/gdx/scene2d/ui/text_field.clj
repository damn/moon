(ns clojure.gdx.scene2d.ui.text-field
  (:require [moon.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField)))

(defn create [{:keys [text skin] :as opts}]
  (doto (TextField. ^String text ^Skin skin)
    (actor/set-opts! opts)))

(defn text [^TextField text-field]
  (.getText text-field))

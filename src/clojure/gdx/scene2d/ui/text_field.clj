(ns clojure.gdx.scene2d.ui.text-field
  (:require [clojure.scene2d.actor :as actor]
            clojure.scene2d.ui.text-field)
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField)))

(defn create [{:keys [text skin] :as opts}]
  (doto (TextField. ^String text ^Skin skin)
    (actor/set-opts! opts)))

(extend-type TextField
  clojure.scene2d.ui.text-field/TextField
  (text [text-field]
    (.getText text-field)))

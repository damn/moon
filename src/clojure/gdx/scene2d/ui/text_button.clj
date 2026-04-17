(ns clojure.gdx.scene2d.ui.text-button
  (:require [moon.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton)))

(defn create
  [{:keys [text skin] :as opts}]
  (doto (TextButton. ^String text ^Skin skin)
    (actor/set-opts! opts)))

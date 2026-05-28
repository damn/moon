(ns clojure.gdx.scenes.scene2d.ui.text-button
  (:require [clojure.gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton)))

(defn create
  [{:keys [text skin] :as opts}]
  (doto (TextButton. ^String text ^Skin skin)
    (actor/set-opts! opts)))

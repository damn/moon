(ns gdx.scenes.scene2d.ui.image-button
  (:require [gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui ImageButton)))

(defn create
  [{:keys [drawable] :as opts}]
  (doto (ImageButton. drawable)
    (actor/set-opts! opts)))

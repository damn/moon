(ns gdx.actor.group.widget.table.button.image-button
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]))

(defn create
  [drawable]
  (image-button/new drawable))

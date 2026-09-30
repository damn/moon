(ns gdx.actor.group.widget.table.button.image
  (:import (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn create [drawable]
  (ImageButton. ^Drawable drawable))

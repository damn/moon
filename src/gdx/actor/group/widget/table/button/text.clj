(ns gdx.actor.group.widget.table.button.text
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin TextButton)))

(defn create [text skin]
  (TextButton. ^String text ^Skin skin))

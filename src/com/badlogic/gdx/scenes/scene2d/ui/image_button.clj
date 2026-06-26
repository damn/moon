(ns com.badlogic.gdx.scenes.scene2d.ui.image-button
  (:require [com.badlogic.gdx.scenes.scene2d.utils.drawable :as drawable])
  (:import (com.badlogic.gdx.scenes.scene2d.ui ImageButton)))

(defn create [image-up]
  (ImageButton. (drawable/type-hint image-up)))

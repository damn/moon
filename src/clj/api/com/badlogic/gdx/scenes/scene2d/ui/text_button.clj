(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-button
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton)))

(defn create [text skin]
  (TextButton. text skin))

(ns clojure.gdx.scene2d.ui.text-button
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton)))

(defn create [text skin]
  (TextButton. ^String text ^Skin skin))

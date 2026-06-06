(ns gdx.scene2d.ui.text-button
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton)))

(defn create
  [{:keys [text skin]}]
  (TextButton. ^String text ^Skin skin))

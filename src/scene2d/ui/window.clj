(ns scene2d.ui.window
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin Window)))

(defn f [{:keys [title skin]}]
  (Window. ^String title ^Skin skin))

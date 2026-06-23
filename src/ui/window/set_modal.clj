(ns ui.window.set-modal
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn f! [^Window window modal?]
  (.setModal window modal?))

(ns clojure.gdx.scene2d.ui.window
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn set-modal! [^Window window modal?]
  (.setModal window modal?))

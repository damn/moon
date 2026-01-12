(ns moon.ui.tooltip
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextTooltip)))

(defn add! [actor tooltip-text ^Skin skin]
  (.addListener actor (TextTooltip. (str tooltip-text) skin))
  actor)

(defn remove! [actor]
  ; TODO
  ;(.removeListener actor (.getListeners actor))
  ; ... first find the listener
  )

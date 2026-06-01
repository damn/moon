(ns editor.app.resize
  (:require [gdx.viewport :as viewport]))

(defn resize!
  [{:keys [ctx/ui-viewport]} width height]
  (viewport/update! ui-viewport width height true))

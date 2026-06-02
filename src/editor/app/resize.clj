(ns editor.app.resize
  (:require [clojure.gdx.utils.viewport :as viewport]))

(defn resize!
  [{:keys [ctx/ui-viewport]} width height]
  (viewport/update! ui-viewport width height true))

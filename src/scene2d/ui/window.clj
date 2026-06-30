(ns scene2d.ui.window
  (:require [clojure.gdx :as gdx]))

(defn f [{:keys [title skin]}]
  (gdx/window ^String title ^Skin skin))

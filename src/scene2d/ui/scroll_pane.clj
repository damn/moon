(ns scene2d.ui.scroll-pane
  (:require [clojure.gdx :as gdx]))

(defn create
  [{:keys [actor skin]}]
  (gdx/scroll-pane actor ^Skin skin))

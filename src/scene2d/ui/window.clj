(ns scene2d.ui.window
  (:require [clojure.gdx.window.new :as new-window]))

(defn f [{:keys [title skin]}]
  (new-window/f title skin))

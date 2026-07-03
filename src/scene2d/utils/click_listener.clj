(ns scene2d.utils.click-listener
  (:require [clojure.gdx.click-listener.new :as new-click-listener]))

(defn create [f]
  (new-click-listener/f f))

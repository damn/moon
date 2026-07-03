(ns scene2d.utils.change-listener
  (:require [clojure.gdx.change-listener.new :as new-change-listener]))

(defn create [f]
  (new-change-listener/f f))

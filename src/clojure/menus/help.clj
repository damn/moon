(ns clojure.menus.help
  (:require [clojure.moon.create-controls-info :refer [controls-info]]))

(def item
  {:label "Help"
   :items [{:label controls-info}]})

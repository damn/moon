(ns clojure.menus.help
  (:require [clojure.moon.controls-info :refer [controls-info]]))

(def item
  {:label "Help"
   :items [{:label controls-info}]})

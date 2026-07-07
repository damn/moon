(ns clojure.species
  (:require [clojure.string :as str]))

(defn f [species _ctx]
  (str "Creature - " (str/capitalize (name species))))

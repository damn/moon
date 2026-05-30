(ns create.default-font
  (:require [game.app :as app]))

(defn step
  [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/default-font
         (app/new-font app
                       {:path "exocet/films.EXL_____.ttf"
                        :size 16
                        :quality-scaling 2})))

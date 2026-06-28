(ns scene2d.stage.act
  (:import (scene2d Stage)))

(defn act! [^Stage stage]
  (.act stage))

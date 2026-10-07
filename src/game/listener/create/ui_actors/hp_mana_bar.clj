(ns game.listener.create.ui-actors.hp-mana-bar
  (:require [game.shared :refer [draw-fn-text draw-fn-texture-region]]
            [moon.number :as number]
            [moon.stats :as stats]
            [moon.textures :as textures]
            [moon.val-max :as val-max])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn hp-mana-bar-create
  [default-font stage textures unit-scale player-eid]
  (let [{:keys [rahmen-file
                rahmenw
                rahmenh
                hpcontent-file
                manacontent-file
                y-mana]} {:rahmen-file "images/rahmen.png"
                          :rahmenw 150
                          :rahmenh 26
                          :hpcontent-file "images/hp.png"
                          :manacontent-file "images/mana.png"
                          :y-mana 80}
        [x y-mana] [(/ (.getWorldWidth (.getViewport ^Stage stage)) 2)
                    y-mana]
        rahmen-tex-reg (textures/texture-region textures {:image/file rahmen-file})
        y-hp (+ y-mana rahmenh)
        draw-hpmana-bar! (fn [ctx batch x y content-file minmaxval name]
                           (draw-fn-texture-region batch unit-scale rahmen-tex-reg [x y])
                           (draw-fn-texture-region batch unit-scale
                                                   (textures/texture-region textures
                                                                            {:image/file content-file
                                                                             :image/bounds [0 0 (* rahmenw (val-max/ratio minmaxval)) rahmenh]})
                                                   [x y])
                           (draw-fn-text batch default-font unit-scale {:text (str (number/readable (minmaxval 0))
                                                         "/"
                                                         (minmaxval 1)
                                                         " "
                                                         name)
                                              :x (+ x 75)
                                              :y (+ y 2)
                                              :up? true}))]
    (proxy [com.badlogic.gdx.scenes.scene2d.Actor] []
      (act [delta]
        (let [^com.badlogic.gdx.scenes.scene2d.Actor this this]
          (proxy-super act delta)))
      (draw [batch parent-alpha]
        (when (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor this)
          (let [stats (:entity/stats @@player-eid)
                bar-x (- x (/ rahmenw 2))]
            (draw-hpmana-bar! nil batch bar-x y-hp hpcontent-file (stats/get-hitpoints stats) "HP")
            (draw-hpmana-bar! nil batch bar-x y-mana manacontent-file (stats/get-mana stats) "MP")))))))

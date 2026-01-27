(ns moon.ui-actors.hp-mana-bar
  (:require [moon.stats :as stats]
            [moon.ctx :as ctx]
            [moon.textures :as textures]
            [moon.readable :as readable]
            [moon.val-max :as val-max])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.utils.viewport Viewport)
           (moon Stage)))

(defn- create-hp-mana-bar* [create-draws]
  (proxy [Actor] []
    (draw [_batch _parent-alpha]
      (when-let [^Stage stage (Actor/.getStage this)]
        (ctx/draw! (.ctx stage)
                   (create-draws (.ctx stage)))))))

(let [config {:rahmen-file "images/rahmen.png"
              :rahmenw 150
              :rahmenh 26
              :hpcontent-file "images/hp.png"
              :manacontent-file "images/mana.png"
              :y-mana 80}]
  (defn- hp-mana-bar-config
    [textures stage]
    (let [{:keys [rahmen-file
                  rahmenw
                  rahmenh
                  hpcontent-file
                  manacontent-file
                  y-mana]} config
          [x y-mana] [(/ (Viewport/.getWorldWidth (Stage/.getViewport stage)) 2)
                      y-mana]
          rahmen-tex-reg (textures/texture-region textures {:image/file rahmen-file})
          y-hp (+ y-mana rahmenh)
          render-hpmana-bar (fn [x y content-file minmaxval name]
                              [[:draw/texture-region rahmen-tex-reg [x y]]
                               [:draw/texture-region
                                (textures/texture-region textures
                                                         {:image/file content-file
                                                          :image/bounds [0 0 (* rahmenw (val-max/ratio minmaxval)) rahmenh]})
                                [x y]]
                               [:draw/text {:text (str (readable/number (minmaxval 0))
                                                       "/"
                                                       (minmaxval 1)
                                                       " "
                                                       name)
                                            :x (+ x 75)
                                            :y (+ y 2)
                                            :up? true}]])]
      (fn [{:keys [ctx/player-eid]}]
        (let [stats (:entity/stats @player-eid)
              x (- x (/ rahmenw 2))]
          (concat
           (render-hpmana-bar x y-hp   hpcontent-file   (stats/get-hitpoints stats) "HP")
           (render-hpmana-bar x y-mana manacontent-file (stats/get-mana      stats) "MP")))))))

(defn create
  [{:keys [ctx/textures
           ctx/stage]}]
  (create-hp-mana-bar* (hp-mana-bar-config textures stage)))

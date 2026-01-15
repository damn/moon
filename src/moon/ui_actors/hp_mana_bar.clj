(ns moon.ui-actors.hp-mana-bar
  (:require [moon.entity.stats :as stats]
            [moon.graphics :as graphics]
            [moon.ui :as ui]
            [moon.utils :as utils]
            [moon.val-max :as val-max])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn- create-hp-mana-bar* [create-draws]
  (proxy [Actor] []
    (draw [_batch _parent-alpha]
      (when-let [stage (.getStage this)]
        (graphics/draw! (:ctx/graphics (.ctx stage))
                        (create-draws (.ctx stage)))))))

(let [config {:rahmen-file "images/rahmen.png"
              :rahmenw 150
              :rahmenh 26
              :hpcontent-file "images/hp.png"
              :manacontent-file "images/mana.png"
              :y-mana 80}]
  (defn- hp-mana-bar-config
    [graphics stage]
    (let [{:keys [rahmen-file
                  rahmenw
                  rahmenh
                  hpcontent-file
                  manacontent-file
                  y-mana]} config
          [x y-mana] [(/ (ui/viewport-width stage) 2)
                      y-mana]
          rahmen-tex-reg (graphics/texture-region graphics {:image/file rahmen-file})
          y-hp (+ y-mana rahmenh)
          render-hpmana-bar (fn [x y content-file minmaxval name]
                              [[:draw/texture-region rahmen-tex-reg [x y]]
                               [:draw/texture-region
                                (graphics/texture-region graphics
                                                         {:image/file content-file
                                                          :image/bounds [0 0 (* rahmenw (val-max/ratio minmaxval)) rahmenh]})
                                [x y]]
                               [:draw/text {:text (str (utils/readable-number (minmaxval 0))
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
  [{:keys [ctx/graphics ; we dont need full graphics -> flatten ctx !!
           ctx/stage]}]
  (create-hp-mana-bar* (hp-mana-bar-config graphics stage)))

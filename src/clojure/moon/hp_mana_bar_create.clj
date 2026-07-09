(ns clojure.moon.hp-mana-bar-create
  (:require [clojure.scene2d.actor.get-stage]
            [clojure.draw :refer [draw!]]
            [clojure.stats.get-hitpoints :as get-hitpoints]
            [clojure.stats.get-mana :as get-mana]
            [clojure.moon-textures :as textures]
            [clojure.readable :as readable]
            [clojure.scene2d-actor :as actor]
            [clojure.val-max.ratio :as ratio]))

(defn hp-mana-bar-create
  [{:keys [ctx/textures
           ctx/stage]}]
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
        [x y-mana] [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                    y-mana]
        rahmen-tex-reg (textures/texture-region textures {:image/file rahmen-file})
        y-hp (+ y-mana rahmenh)
        render-hpmana-bar (fn [x y content-file minmaxval name]
                            [[:draw/texture-region rahmen-tex-reg [x y]]
                             [:draw/texture-region
                              (textures/texture-region textures
                                                       {:image/file content-file
                                                        :image/bounds [0 0 (* rahmenw (ratio/f minmaxval)) rahmenh]})
                              [x y]]
                             [:draw/text {:text (str (readable/f (minmaxval 0))
                                                     "/"
                                                     (minmaxval 1)
                                                     " "
                                                     name)
                                          :x (+ x 75)
                                          :y (+ y 2)
                                          :up? true}]])
        create-draws (fn [{:keys [ctx/player-eid]}]
                       (let [stats (:entity/stats @player-eid)
                             x (- x (/ rahmenw 2))]
                         (concat
                          (render-hpmana-bar x y-hp hpcontent-file (get-hitpoints/f stats) "HP")
                          (render-hpmana-bar x y-mana manacontent-file (get-mana/f stats) "MP"))))]
    (actor/f
     {:draw! (fn [this _batch _parent-alpha]
               (when-let [stage (clojure.scene2d.actor.get-stage/f this)]
                 (draw! (:stage/ctx stage)
                        (create-draws (:stage/ctx stage)))))})))

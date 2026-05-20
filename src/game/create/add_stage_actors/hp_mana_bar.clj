(ns game.create.add-stage-actors.hp-mana-bar
  (:require [gdl.scene2d.actor :as actor]
            [moon.draws :as draws]
            [moon.number :as number]
            [moon.stats :as stats]
            [moon.textures :as textures]
            [moon.val-max :as val-max]))

(defn create
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
                                                        :image/bounds [0 0 (* rahmenw (val-max/ratio minmaxval)) rahmenh]})
                              [x y]]
                             [:draw/text {:text (str (number/readable (minmaxval 0))
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
                          (render-hpmana-bar x y-hp   hpcontent-file   (stats/get-hitpoints stats) "HP")
                          (render-hpmana-bar x y-mana manacontent-file (stats/get-mana      stats) "MP"))))]
    {:type :ui/actor
     :draw! (fn [this _batch _parent-alpha]
              (when-let [stage (actor/stage this)]
                (draws/handle (:stage/ctx stage)
                              (create-draws (:stage/ctx stage)))))}))

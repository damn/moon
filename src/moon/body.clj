(ns moon.body)

(defprotocol Body
  (rectangle [_])
  (touched-tiles [_])
  (overlaps? [_ other-body])
  (distance [_ other-body])
  (direction [_ other-body]))


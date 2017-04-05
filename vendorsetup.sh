#sh device/samsung/o7prolte/patches/apply.sh
function o7prolte
{
export WITH_SU=true
lunch cm_o7prolte-userdebug
make -j4 bacon
}
